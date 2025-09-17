# Telegram Bot Setup for CI Notifications

This document explains how to set up Telegram bot notifications for your CI/CD pipeline.

## 🤖 Creating a Telegram Bot

1. **Create a new bot:**
   - Open Telegram and search for `@BotFather`
   - Send `/newbot` command
   - Follow the instructions to create your bot
   - Save the **Bot Token** (format: `123456789:ABCdefGHIjklMNOpqrsTUVwxyz`)

2. **Get your Chat ID:**
   - Add your bot to a channel or start a private chat
   - Send any message to the bot
   - Visit: `https://api.telegram.org/bot<YOUR_BOT_TOKEN>/getUpdates`
   - Find your chat ID in the response (it's a number, can be negative for channels)

## 🔐 Setting up GitHub Secrets

1. Go to your GitHub repository
2. Navigate to **Settings** → **Secrets and variables** → **Actions**
3. Click **New repository secret**
4. Add these two secrets:

   | Secret Name | Value | Description |
   |-------------|-------|-------------|
   | `TELEGRAM_BOT_TOKEN` | Your bot token from BotFather | Token to authenticate with Telegram API |
   | `TELEGRAM_CHAT_ID` | Your chat/channel ID | Where to send the notifications |

## 📱 Message Format

The bot will send notifications with the following information:

```
🚀 Test Execution Report ✅

Status: SUCCESS
Run Number: 44
Repository: NoBugs-Projects/w1
Branch: main
Commit: abc123def456

📊 Test Report: https://nobugs-projects.github.io/w1/44/

🔗 Allure Report: https://nobugs-projects.github.io/w1/44/allure-report/
🔍 Swagger Coverage: https://nobugs-projects.github.io/w1/44/swagger-coverage-report/

Workflow: Report
Triggered by: username
```

## 🔧 Features

- **Status Indicators**: ✅ Success, ❌ Failed, ⚠️ Cancelled
- **Direct Links**: Clickable links to test reports
- **Run Information**: Repository, branch, commit details
- **Always Executes**: Sends notification regardless of test results
- **Markdown Formatting**: Clean, readable message format

## 🚀 Testing

To test the setup:
1. Push changes to trigger the workflow
2. Check if the notification appears in your Telegram chat/channel
3. Verify all links work correctly

## 📝 Notes

- The bot will send notifications after the report is published
- Links point to the GitHub Pages hosted reports
- Run number is automatically extracted from the workflow
- Message includes both the main dashboard and individual report links
