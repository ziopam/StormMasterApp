import json

from asgiref.sync import sync_to_async

from roomApp.models import Message, Idea
from roomApp.round_robin_handlers import get_users_current_ideas, get_user_current_idea


class ReceivedHandlers:
    """
    This class contains the handlers for received messages from the WebSocket.
    """


    async def new_message_received(self, room, text_data_json):
        """
        This function is called when a new message is received
        :param room: room in which the message is sent
        :param text_data_json: the message data
        :return:
        """

        message = text_data_json['message']
        if message:
            # Save the message to the database
            message_object = await Message.objects.acreate(
                room=room,
                sender=self.scope['user'],
                text=message
            )

            # Send the message to the room group
            await self.channel_layer.group_send(
                self.room_group_name,
                {
                    'type': 'new_message',
                    'id': message_object.id,
                    'username': self.scope['user'].username,
                    'message': message
                }
            )

    async def set_idea_received(self, text_data_json):
        """
        This function is called when a new idea is set
        :param text_data_json: the message and its idea data
        :return:
        """

        message_id = text_data_json['message_id']
        idea_number = text_data_json['idea_number']

        # Check if idea_number is an integer
        if not isinstance(idea_number, int) or not message_id:
            return

        if 1 <= idea_number <= 1000:
            room = await self.get_room()
            if room is None:
                return

            message = await sync_to_async(Message.objects.get)(id=message_id)
            idea, _ = await sync_to_async(Idea.objects.get_or_create)(room=room, idea_number=idea_number)
            message.idea = idea
            await message.asave()
            await sync_to_async(idea.update_votes)()
            idea_votes = idea.votes

            # Send the message to the room group
            await self.channel_layer.group_send(
                self.room_group_name,
                {
                    'type': 'set_idea',
                    'message_id': message.id,
                    'idea_number': idea_number,
                    'idea_votes': idea_votes
                }
            )

    async def remove_idea_received(self, text_data_json):
        """
        This function is called when the idea is removed
        :param text_data_json: the message data with the idea to remove
        :return:
        """

        message_id = text_data_json['message_id']
        message = await sync_to_async(Message.objects.get)(id=message_id)
        message.idea = None
        await message.asave()
        # Send the message to the room group
        await self.channel_layer.group_send(
            self.room_group_name,
            {
                'type': 'set_idea',
                'message_id': message.id,
                'idea_number': -1,
                'idea_votes': 0
            }
        )

    async def vote_for_idea_received(self, text_data_json):
        """
        This function is called when the user votes for an idea
        :param text_data_json: the idea data
        :return:
        """

        idea_number = text_data_json['idea_number']
        try:
            room = await self.get_room()
            if room is None:
                return
            idea = await sync_to_async(Idea.objects.get)(room=room, idea_number=idea_number)
        except Idea.DoesNotExist:
            return
        user = self.scope['user']

        # Check if the user has already voted for this idea
        if await idea.voters.acontains(user):
            return

        await sync_to_async(idea.voters.add)(user)
        await idea.asave()
        await sync_to_async(idea.update_votes)()

        # Send the message to the room group
        await self.channel_layer.group_send(
            self.room_group_name,
            {
                'type': 'update_votes',
                'idea_number': idea_number,
                'idea_votes': idea.votes
            })

    async def devote_for_idea_received(self, text_data_json):
        """
        This function is called when the user devotes for an idea
        :param text_data_json: the idea data
        :return:
        """

        idea_number = text_data_json['idea_number']
        try:
            room = await self.get_room()
            if room is None:
                return
            idea = await sync_to_async(Idea.objects.get)(room=room, idea_number=idea_number)
        except Idea.DoesNotExist:
            return
        user = self.scope['user']

        # Check if the user did not vote for this idea
        if not await idea.voters.acontains(user):
            return

        await sync_to_async(idea.voters.remove)(user)
        await idea.asave()
        await sync_to_async(idea.update_votes)()

        # Send the message to the room group
        await self.channel_layer.group_send(
            self.room_group_name,
            {
                'type': 'update_votes',
                'idea_number': idea_number,
                'idea_votes': idea.votes
            })

    async def round_robin_update_received(self, text_data_json):
        """
        This function is called when the Round Robin data is updated
        :param text_data_json: the message data with the updated Round Robin data
        """

        room = await self.get_room()
        if room is None:
            return

        room_type_name = await sync_to_async(lambda: room.room_type.name)()

        if room_type_name == 'Round Robin':
            user = self.scope['user']
            new_text = text_data_json['new_text']

            robin_data = await sync_to_async(lambda: room.round_robin_data)()
            participants_amount = await room.participants.acount()
            current_idea = await get_user_current_idea(user, room, robin_data)

            # Find the message to change
            message_to_change = await Message.objects.aget(
                idea=current_idea,
                room=room
            )
            message_to_change.text = new_text
            await message_to_change.asave()

            # Send message to the sender that the message was successfully changed
            await self.send(text_data=json.dumps({
                'type': 'round_robin_updated'
            }))

            robin_data.received_ideas += 1

            # Everybody has sent their ideas. Round is completed
            if robin_data.received_ideas >= participants_amount:
                robin_data.completed_rounds += 1
                robin_data.received_ideas = 0

                # Check if all rounds are completed
                if robin_data.completed_rounds >= participants_amount:
                    messages = await sync_to_async(lambda: Message.objects.filter(room=room).
                                                   order_by('timestamp').
                                                   values('id', 'sender__username', 'text', 'idea__idea_number',
                                                          'idea__votes'))()

                    # Inform all users that the Round Robin is finished
                    await self.channel_layer.group_send(
                        self.room_group_name,
                        {
                            'type': 'round_robin_finished',
                            'details': room.details,
                            'messages': await sync_to_async(list)(messages)
                        })

                    await robin_data.adelete()
                else:
                    users_ideas = await get_users_current_ideas(room, robin_data)

                    # Send the updated ideas to all users
                    await self.channel_layer.group_send(
                        self.room_group_name,
                        {
                            'type': 'new_round_started',
                            'users_ideas': users_ideas
                        }
                    )
            await robin_data.asave()

