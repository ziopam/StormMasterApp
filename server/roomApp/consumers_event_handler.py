import json

class EventHandlers:
    """
    This class contains the event handlers for the chat room
    """

    async def user_joined(self, event):
        """
        This function is called when a user joins the room
        :param event: event object
        """

        username = event['username']
        await self.send(text_data=json.dumps({
            'type': 'user_joined',
            'username': username
        }))

    async def user_left(self, event):
        """
        This function is called when a user leaves the room
        :param event: event object
        """

        username = event['username']
        await self.send(text_data=json.dumps({
            'type': 'user_left',
            'username': username
        }))

    async def chat_started(self, event):
        """
        This function is called when the chat is started
        :param event: event object (not used)
        """

        details = event['details']
        room_type = event['room_type']
        await self.send(text_data=json.dumps({
            'type': 'chat_started',
            'details': details,
            'room_type': room_type
        }))

    async def new_message(self, event):
        """
        This function is called when a new message is received
        :param event: event object
        """

        username = event['username']
        message = event['message']
        id = event['id']
        await self.send(text_data=json.dumps({
            'type': 'new_message',
            'username': username,
            'id': id,
            'message': message
        }))

    async def set_idea(self, event):
        """
        This function is called when a new idea is set
        :param event: event object
        """

        message_id = event['message_id']
        idea_number = event['idea_number']
        idea_votes = event['idea_votes']
        await self.send(text_data=json.dumps({
            'type': 'set_idea',
            'message_id': message_id,
            'idea_number': idea_number,
            'idea_votes': idea_votes
        }))

    async def room_deleted(self, event):
        """
        This function is called when the room is deleted
        :param event: event object (not used)
        """

        await self.close(code=4004, reason="Room deleted")

    async def update_votes(self, event):
        """
        This function is called when the votes are updated
        :param event: event object
        """

        idea_number = event['idea_number']
        idea_votes = event['idea_votes']
        await self.send(text_data=json.dumps({
            'type': 'update_votes',
            'idea_number': idea_number,
            'idea_votes': idea_votes
        }))

    async def finish_brainstorm(self, event):
        """
        This function is called when the brainstorm is finished
        :param event: event object
        """

        await self.close(code=4100, reason="Brainstorm finished")