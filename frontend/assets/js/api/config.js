const currentHost = window.location.hostname;
const isLocalHost =
  currentHost === "localhost" ||
  currentHost === "127.0.0.1" ||
  /^10\./.test(currentHost) ||
  /^192\.168\./.test(currentHost) ||
  /^172\.(1[6-9]|2\d|3[01])\./.test(currentHost);

export const API_BASE = isLocalHost ? `http://${currentHost}:8080` : "https://api.hoangdev.com";
