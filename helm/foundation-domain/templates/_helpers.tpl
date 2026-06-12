{{- define "foundation-domain.labels" -}}
app.kubernetes.io/name: {{ .Chart.Name }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/version: {{ .Chart.AppVersion }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
helm.sh/chart: {{ .Chart.Name }}-{{ .Chart.Version }}
domain: foundation
{{- end }}

{{- define "foundation-domain.selectorLabels" -}}
app: {{ .serviceName }}
domain: foundation
{{- end }}

{{- define "foundation-domain.serviceLabels" -}}
{{ include "foundation-domain.labels" . }}
{{ include "foundation-domain.selectorLabels" . }}
{{- end }}

{{- define "foundation-domain.fullname" -}}
{{ .Release.Name }}-{{ .Chart.Name }}
{{- end }}

{{- define "foundation-domain.image" -}}
{{ .Values.global.registry }}/{{ .Values.global.org }}/{{ .Values.global.repo }}-{{ .serviceName }}:{{ .Values.global.imageTag | default .Chart.AppVersion }}
{{- end }}
