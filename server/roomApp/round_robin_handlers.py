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

