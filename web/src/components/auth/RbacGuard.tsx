import { useAuthStore } from '@/stores';
import type { Permission, Role } from '@/types';

interface RbacGuardProps {
  children: React.ReactNode;
  permission?: Permission;
  permissions?: Permission[];
  role?: Role;
  requireAny?: boolean;
  fallback?: React.ReactNode;
}

export function RbacGuard({
  children,
  permission,
  permissions,
  role,
  requireAny = false,
  fallback = null,
}: RbacGuardProps) {
  const { hasPermission, hasAnyPermission, hasRole } = useAuthStore();

  if (permission && !hasPermission(permission)) return <>{fallback}</>;

  if (permissions?.length) {
    const hasAccess = requireAny
      ? hasAnyPermission(...permissions)
      : permissions.every((p) => hasPermission(p));
    if (!hasAccess) return <>{fallback}</>;
  }

  if (role && !hasRole(role)) return <>{fallback}</>;

  return <>{children}</>;
}
