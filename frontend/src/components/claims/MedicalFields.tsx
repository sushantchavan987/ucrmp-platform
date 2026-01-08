import { useFormContext } from 'react-hook-form';
import { Input } from '../ui/Input';

export const MedicalFields = () => {
  const { register, formState: { errors } } = useFormContext();

  // FIX: Added eslint disable comment
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const metadataErrors = errors.metadata as any;

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 gap-6 animate-in fade-in slide-in-from-top-2">
      <Input
        label="Hospital / Clinic Name"
        placeholder="e.g. City General Hospital"
        {...register('metadata.hospitalName')}
        error={metadataErrors?.hospitalName?.message}
      />
      
      <Input
        label="Prescription Number"
        placeholder="Rx-998877"
        {...register('metadata.prescriptionNumber')}
        error={metadataErrors?.prescriptionNumber?.message}
      />
    </div>
  );
};