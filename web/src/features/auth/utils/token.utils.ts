

import { TOKEN_CONFIG } from '../constants';

interface JwtPayload {
  sub: string;
  exp: number;
  iat: number;
  roles?: string[];
  permissions?: string[];
  [key: string]: unknown;
}

export function decodeToken(token: string): JwtPayload | null {
  try {
    const parts = token.split('.');
    if (parts.length !== 3) return null;

    const payload = parts[1];
    const decoded = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));
    return JSON.parse(decoded) as JwtPayload;
  } catch {
    return null;
  }
}

export function isTokenExpired(token: string): boolean {
  const payload = decodeToken(token);
  if (!payload?.exp) return true;

  const expiryTime = payload.exp * 1000;
  return Date.now() >= expiryTime;
}

export function shouldRefreshToken(token: string): boolean {
  const payload = decodeToken(token);
  if (!payload?.exp) return true;

  const expiryTime = payload.exp * 1000;
  const refreshThreshold = TOKEN_CONFIG.REFRESH_THRESHOLD_MS;

  return Date.now() >= expiryTime - refreshThreshold;
}

export function getTokenRemainingTime(token: string): number {
  const payload = decodeToken(token);
  if (!payload?.exp) return 0;

  const expiryTime = payload.exp * 1000;
  const remaining = expiryTime - Date.now();

  return Math.max(0, remaining);
}

export function getUserIdFromToken(token: string): string | null {
  const payload = decodeToken(token);
  return payload?.sub || null;
}

export function getRolesFromToken(token: string): string[] {
  const payload = decodeToken(token);
  return payload?.roles || [];
}

export function formatAuthHeader(token: string, type = 'Bearer'): string {
  return `${type} ${token}`;
}
