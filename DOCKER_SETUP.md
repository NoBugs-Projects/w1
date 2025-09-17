# Docker Compose Setup for TeamCity Testing Environment

This setup provides a complete testing environment with TeamCity server, agent, and Selenoid for browser automation.

## Prerequisites

- Docker Desktop installed and running
- Docker Compose available (included with Docker Desktop)

## Quick Start

1. **Start all services:**
   ```bash
   ./docker-manage.sh start
   ```

2. **Check service status:**
   ```bash
   ./docker-manage.sh status
   ```

3. **View logs:**
   ```bash
   # All services
   ./docker-manage.sh logs
   
   # Specific service
   ./docker-manage.sh logs teamcity-server
   ```

4. **Stop all services:**
   ```bash
   ./docker-manage.sh stop
   ```

## Services

### TeamCity Server
- **URL:** http://localhost:8111
- **Purpose:** CI/CD server for running tests
- **Port:** 8111

### TeamCity Agent
- **Purpose:** Executes tests and builds
- **Depends on:** TeamCity Server

### Selenoid
- **URL:** http://localhost:4444
- **Purpose:** Browser automation service
- **Port:** 4444

### Selenoid UI
- **URL:** http://localhost:8080
- **Purpose:** Web interface for Selenoid
- **Port:** 8080

## Management Commands

| Command | Description |
|---------|-------------|
| `./docker-manage.sh start` | Start all services |
| `./docker-manage.sh stop` | Stop all services |
| `./docker-manage.sh restart` | Restart all services |
| `./docker-manage.sh status` | Show service status |
| `./docker-manage.sh logs` | Show logs for all services |
| `./docker-manage.sh logs [SERVICE]` | Show logs for specific service |
| `./docker-manage.sh pull` | Pull latest Docker images |
| `./docker-manage.sh cleanup` | Remove all containers and data |
| `./docker-manage.sh help` | Show help message |

## Directory Structure

```
tmp/
├── teamcity_server/
│   ├── datadir/          # TeamCity server data
│   └── logs/             # TeamCity server logs
└── teamcity_agent/
    └── conf/             # TeamCity agent configuration
```

## Configuration

- Browser configurations are stored in `config/browsers.json`
- TeamCity server and agent use specific versions (2023.11.1) for stability
- All services run on a custom network for isolation

## Troubleshooting

### Port Conflicts
If you get port conflicts, check if other services are using:
- Port 8111 (TeamCity Server)
- Port 4444 (Selenoid)
- Port 8080 (Selenoid UI)

### Permission Issues
The script creates necessary directories with proper permissions. If you encounter issues:
```bash
sudo chown -R $USER:$USER tmp/
```

### Clean Start
To completely reset the environment:
```bash
./docker-manage.sh cleanup
./docker-manage.sh start
```

## Development

- Services are configured to restart automatically on failure
- Logs are preserved in the `tmp/` directory
- The setup matches the GitHub Actions workflow configuration
