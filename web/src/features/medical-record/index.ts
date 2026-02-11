

export {
  useMedicalRecords,
  useMedicalRecord,
  usePatientRecords,
  usePatientTimeline,
  useCreateMedicalRecord,
  useUpdateMedicalRecord,
  useFinalizeMedicalRecord,
} from './hooks/useMedicalRecord';

export { medicalRecordApi } from './api/medical-record.api';

export type * from './types/medical-record.types';

export * from './components';

export * from './pages';

export * from './constants';

export * from './schemas';

export * from './utils';
