# Hetzner Runner Upgrade: CX33 → CX52

## Problem
39 JVM containers × ~200MB = ~11GB RAM > 8GB (CX33) = 11 OOM-killed containers in smoke test.

## Solution
Upgrade runner-3 (ID: 53566293, tag: `docker`) from CX33 to CX52.

## Current Runner Inventory
| Runner | ID | Tags | Current | Target |
|--------|-----|------|---------|--------|
| runner-2 | 53566292 | (none) | CX33 (8GB) | CX33 (no change) |
| runner-3 | 53566293 | docker | CX33 (8GB) | **CX52 (32GB)** |
| runner-4 | 53566294 | (none) | CX33 (8GB) | CX33 (no change) |

## CX52 Specs
- 8 vCPU
- 32GB RAM
- ~€33.53/month (Hetzner Cloud pricing)
- Total monthly after upgrade: 2×CX33 + 1×CX52 ≈ €51.49

## Upgrade Steps

### Step 1: Create new CX52 server
```bash
# On your local machine (with hcloud CLI)
hcloud server create \
  --name runner-3-cx52 \
  --type cx52 \
  --image ubuntu-24.04 \
  --location eu-central-1 \
  --ssh-key <your-ssh-key-name>
```

### Step 2: Install GitLab Runner on new server
```bash
# SSH into the new server
ssh root@<new-server-ip>

# Install GitLab Runner
curl -L https://packages.gitlab.com/install/repositories/runner/gitlab-runner/script.deb.sh | sudo bash
sudo apt-get install gitlab-runner

# Install Docker
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker gitlab-runner

# Register the runner (use group token from GitLab)
sudo gitlab-runner register \
  --url https://gitlab.com/ \
  --token <group-registration-token> \
  --executor docker \
  --docker-image "docker:24.0.5" \
  --docker-privileged \
  --docker-volumes /var/run/docker.sock:/var/run/docker.sock \
  --docker-volumes /cache \
  --tag-list "docker" \
  --description "hetzner-cx52-runner-3"
```

### Step 3: Verify new runner is online
```bash
# On local machine
curl --header "PRIVATE-TOKEN: <your-token>" \
  "https://gitlab.com/api/v4/groups/133848857/runners" | jq '.[] | select(.description | contains("cx52"))'
```

### Step 4: Delete old runner
```bash
# Delete old runner-3 from GitLab
curl --request DELETE \
  --header "PRIVATE-TOKEN: <your-token>" \
  "https://gitlab.com/api/v4/runners/53566293"

# Delete old Hetzner server
hcloud server delete runner-3  # or the old server name
```

### Step 5: Verify CI picks up new runner
```bash
# Trigger a pipeline manually
curl --request POST \
  --header "PRIVATE-TOKEN: <your-token>" \
  "https://gitlab.com/api/v4/projects/<project-id>/pipeline?ref=dev"
```

## Alternative: Hetzner Cloud Console (No CLI)
1. Go to https://console.hetzner.cloud
2. Click "Create Server" → Ubuntu 24.04 → CX52 → eu-central-1
3. SSH in and follow Steps 2-4 above
4. Delete old server after confirming new one works
