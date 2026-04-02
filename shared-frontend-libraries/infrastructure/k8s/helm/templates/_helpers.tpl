{{ .Chart.Name }} was installed successfully!

The deployment includes:
- {{ .Values.replicaCount }} replica(s)
- Service type: {{ .Values.service.type }}
- Ingress: {{ .Values.ingress.enabled | ternary "enabled" "disabled" }}

To get the application URL:
{{- if .Values.ingress.enabled -}}
{{- range $host := .Values.ingress.hosts }}
  http://{{ $host.host }}/
{{- end }}
{{- else if contains .Values.service.type "LoadBalancer" -}}
  Get external IP from service: kubectl get svc {{ include "shared-frontend-libraries.fullname" . }}
{{- else -}}
  Port forward: kubectl port-forward svc/{{ include "shared-frontend-libraries.fullname" . }} 3000:80
{{- end }}

To view the logs:
  kubectl logs -l app.kubernetes.io/name={{ include "shared-frontend-libraries.name" . }}

To upgrade the deployment:
  helm upgrade shared-frontend-libraries rapidassist/shared-frontend-libraries

To uninstall:
  helm uninstall shared-frontend-libraries
