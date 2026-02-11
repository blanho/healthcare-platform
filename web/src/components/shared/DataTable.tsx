import { useMemo, useCallback } from 'react';
import { DataGrid, type GridColDef, type GridPaginationModel, type DataGridProps } from '@mui/x-data-grid';
import { Box } from '@mui/material';

interface DataTableProps<T extends { id: string | number }> extends Omit<DataGridProps, 'rows' | 'columns' | 'onRowClick'> {
  rows: T[];
  columns: GridColDef[];
  totalElements?: number;
  page?: number;
  pageSize?: number;
  onPageChange?: (page: number, pageSize: number) => void;
  onRowClick?: (row: T) => void;
  isLoading?: boolean;
}

export function DataTable<T extends { id: string | number }>({
  rows,
  columns,
  totalElements,
  page = 0,
  pageSize = 20,
  onPageChange,
  onRowClick,
  isLoading = false,
  ...rest
}: DataTableProps<T>) {
  const paginationModel = useMemo(
    () => ({ page, pageSize }),
    [page, pageSize],
  );

  const handlePaginationChange = useCallback(
    (model: GridPaginationModel) => {
      onPageChange?.(model.page, model.pageSize);
    },
    [onPageChange],
  );

  return (
    <Box sx={{ width: '100%' }}>
      <DataGrid
        rows={rows}
        columns={columns}
        loading={isLoading}
        paginationMode={totalElements !== undefined ? 'server' : 'client'}
        rowCount={totalElements ?? rows.length}
        paginationModel={paginationModel}
        onPaginationModelChange={handlePaginationChange}
        pageSizeOptions={[10, 20, 50]}
        onRowClick={onRowClick ? (params) => onRowClick(params.row as T) : undefined}
        disableRowSelectionOnClick
        autoHeight
        sx={{
          border: 'none',
          '& .MuiDataGrid-cell': {
            cursor: onRowClick ? 'pointer' : 'default',
          },
          '& .MuiDataGrid-columnHeaders': {
            backgroundColor: 'grey.50',
          },
        }}
        {...rest}
      />
    </Box>
  );
}
