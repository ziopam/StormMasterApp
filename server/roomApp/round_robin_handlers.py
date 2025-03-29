from asgiref.sync import sync_to_async

from roomApp.models import Room, Idea, Message, RoundRobinData


def set_up_round_robin(room: Room):
    """
    This function is used to set up the room for the Round Robin.
    Creates an idea for each participant and a message for each participant in the room.
    """

    i = 1
    for user in room.participants.all():
        idea = Idea.objects.create(idea_number=i, room=room)
        i += 1
        Message.objects.create(room=room, sender=user, idea=idea)
    RoundRobinData.objects.create(room=room)

async def get_user_current_idea(user, room: Room, robin_data: RoundRobinData):
    """
    This function is used to get the current idea of the user which they are working on.
    """
    user_message = await Message.objects.aget(sender=user, room=room)  # Here we can find user's idea number
    completed_rounds = robin_data.completed_rounds
    participants_amount = await room.participants.acount()
    idea_number = await sync_to_async(lambda: user_message.idea.idea_number)()

    # Calculate the current idea number for user
    current_idea = idea_number + completed_rounds
    if current_idea > participants_amount:
        current_idea -= participants_amount

    idea = await Idea.objects.aget(idea_number=current_idea, room=room)
    return idea


async def get_users_current_ideas(room: Room, robin_data: RoundRobinData):
    """
    This function is used to get the current ideas of the users which they are working on.
    """

    users_ideas = {}

    async for user in room.participants.all():
        current_idea = await get_user_current_idea(user, room, robin_data)
        current_message = await Message.objects.aget(idea=current_idea, room=room)
        users_ideas[user.username] = current_message.text
    return users_ideas
