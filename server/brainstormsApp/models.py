from django.db import models

from django.contrib.auth import get_user_model

User = get_user_model()

class Brainstorm(models.Model):
    """
    This class is used to define the Brainstorm model
    """

    title = models.CharField(max_length=25, null=False, blank=False)
    creator = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        related_name="created_brainstorms"
    )
    completionDate = models.DateField(auto_now_add=True)
    participants = models.ManyToManyField(
        User,
        related_name="participated_brainstorms"
    )
    details = models.TextField(null=False, blank=False)

    def __str__(self):
        return self.title
