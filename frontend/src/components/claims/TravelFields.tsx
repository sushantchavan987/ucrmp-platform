import { useFormContext } from 'react-hook-form';
import { Input } from '../ui/Input';

export const TravelFields = () => {
  const { register, formState: { errors } } = useFormContext();

  // FIX: Added eslint disable comment
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const metadataErrors = errors.metadata as any;

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 gap-6 animate-in fade-in slide-in-from-top-2">
      <Input
        label="Hotel Name"
        placeholder="e.g. The Grand Hyatt"
        {...register('metadata.hotelName')}
        error={metadataErrors?.hotelName?.message}
      />
      
      <Input
        label="Flight Number"
        placeholder="e.g. UA-1234"
        {...register('metadata.flightNumber')}
        error={metadataErrors?.flightNumber?.message}
      />
    </div>
  );
};