import { Navigate, useLocation } from 'react-router-dom';
import { useAuthStore } from '@/stores';
import type { Permission, Role } from '@/types';

interface ProtectedRouteProps {
  children: React.ReactNode;
  requiredPermission?: Permission;
  requiredPermissions?: Permission[];
  requiredRole?: Role;
  requireAny?: boolean;
}

export function ProtectedRoute({
  children,
  requiredPermission,
  requiredPermissions,
  requiredRole,
  requireAny = false,
}: ProtectedRouteProps) {
  const { isAuthenticated, hasPermission, hasAnyPermission, hasRole } = useAuthStore();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (requiredPermission && !hasPermission(requiredPermission)) {
    return <Navigate to="/unauthorized" replace />;
  }

  if (requiredPermissions?.length) {
    const hasAccess = requireAny
      ? hasAnyPermission(...requiredPermissions)
      : requiredPermissions.every((p) => hasPermission(p));

    if (!hasAccess) {
      return <Navigate to="/unauthorized" replace />;
    }
  }

  if (requiredRole && !hasRole(requiredRole)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return <>{children}</>;
}
