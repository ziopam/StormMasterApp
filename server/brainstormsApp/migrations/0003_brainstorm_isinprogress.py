# Generated by Django 4.2.17 on 2025-01-12 01:16

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('brainstormsApp', '0002_rename_completion_date_brainstorm_completiondate'),
    ]

    operations = [
        migrations.AddField(
            model_name='brainstorm',
            name='isInProgress',
            field=models.BooleanField(default=True),
        ),
    ]
