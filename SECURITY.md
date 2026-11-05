# Security Policy

## Supported Versions

Only the latest release of Declutter Tree receives active security updates and patches.

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | Yes                |
| < 1.0.0 | No                 |

## Reporting a Vulnerability

If you discover a security vulnerability or security bug in Declutter Tree, please report it privately. Do not open a public issue.

### How to Report

1. Go to the repository's **Security** tab: [github.com/vin51435/declutter-tree-intellij/security](https://github.com/vin51435/declutter-tree-intellij/security).
2. Click **Report a vulnerability** to open an advisory draft.
3. Provide a description of the issue, affected versions, and steps to reproduce.

Alternatively, you can email security reports directly to: `v3p51435@gmail.com`.

### Response Timeline

- Initial acknowledgment: within 48 hours.
- Assessment and validation: within 5 business days.
- Fix and disclosure coordination: as soon as a tested patch is ready.

## Security Architecture

Declutter Tree is designed with security and privacy as defaults:
- **No Network Access:** The plugin makes no outbound HTTP/HTTPS requests, telemetry calls, or network operations.
- **Local Read-Only Tree Operations:** File queries operate strictly on in-memory VirtualFile and PSI stubs to read declaration types and file names within open projects.
- **No File Modifications:** The plugin never writes, modifies, or deletes user files or project configurations.
