import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm, FormProvider, useWatch } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { toast } from 'react-hot-toast';
import { createClaimSchema, type CreateClaimFormData } from '../lib/schemas';
import { claimService } from '../services/claimService';
import { Button } from '../components/ui/Button';
import { Input } from '../components/ui/Input';
import { Select } from '../components/ui/Select'; // ✅ Import New Component
import { TravelFields } from '../components/claims/TravelFields';
import { MedicalFields } from '../components/claims/MedicalFields';
import { Plane, Stethoscope, PartyPopper, ArrowLeft } from 'lucide-react';
import { useTitle } from '../hooks/useTitle';
import { logger } from '../lib/utils';
import { CLAIM_TYPES, CLAIM_TYPE_OPTIONS } from '../utils/constants';

// start
const CreateClaimPage = () => {
  useTitle('New Claim');
  const navigate = useNavigate();

  const methods = useForm<CreateClaimFormData>({
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    resolver: zodResolver(createClaimSchema) as any, 
    mode: "onBlur",
    defaultValues: {
      claimType: CLAIM_TYPES.TRAVEL,
      amount: undefined, 
      description: '',
      metadata: { hotelName: '', flightNumber: '' }
    }
  });

  const claimType = useWatch({ control: methods.control, name: 'claimType' });

  const onSubmit = async (data: CreateClaimFormData) => {
    logger.info("📝 Submitting claim", data);
    await toast.promise(
      claimService.createClaim(data),
      {
        loading: 'Submitting claim...',
        success: () => {
           setTimeout(() => navigate('/dashboard'), 1000);
           return 'Claim submitted successfully!';
        },
        error: 'Failed to submit. Try again.'
      }
    );
  };

  useEffect(() => { logger.info("📱 [UI] Create Claim Page Mounted"); }, []);

  return (
    <div className="w-full max-w-[1600px] mx-auto px-4 sm:px-6 lg:px-8 py-6">
      
      <div className="mb-6">
        <button 
          onClick={() => navigate('/dashboard')}
          className="group flex items-center text-sm font-medium text-slate-500 hover:text-slate-900 transition-colors"
        >
          <div className="w-8 h-8 rounded-full bg-white border border-slate-200 flex items-center justify-center mr-2 group-hover:border-brand-300 group-hover:text-brand-600 transition-all shadow-sm">
            <ArrowLeft size={16} strokeWidth={2.5} className="group-hover:-translate-x-0.5 transition-transform" />
          </div>
          Back to Dashboard
        </button>
      </div>

      <div className="max-w-4xl mx-auto bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
        
        <div className="bg-slate-50/50 border-b border-slate-100 p-8 pb-6">
          <div className="flex items-center gap-4">
            <div className="p-3 bg-brand-50 rounded-xl border border-brand-100 text-brand-600">
                {claimType === CLAIM_TYPES.TRAVEL && <Plane size={24} />}
                {claimType === CLAIM_TYPES.MEDICAL && <Stethoscope size={24} />}
                {claimType === CLAIM_TYPES.ENTERTAINMENT && <PartyPopper size={24} />}
            </div>
            <div>
                <h1 className="text-xl font-bold text-slate-900">New Reimbursement Claim</h1>
                <p className="text-slate-500 text-sm mt-1">Fill in the details below to request approval.</p>
            </div>
          </div>
        </div>

        <div className="p-8">
          <FormProvider {...methods}>
            <form onSubmit={methods.handleSubmit(onSubmit)} className="space-y-8">
              
              <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                
                {/* ✅ REPLACED HARDCODED HTML WITH SELECT COMPONENT */}
                <Select
                  label="Claim Category"
                  options={CLAIM_TYPE_OPTIONS}
                  disabled={methods.formState.isSubmitting}
                  {...methods.register('claimType')}
                />

                <Input
                  label="Amount"
                  type="number"
                  step="0.01"
                  min="0"
                  autoFocus
                  disabled={methods.formState.isSubmitting}
                  placeholder="0.00"
                  prefixIcon={<span className="font-semibold text-slate-500">$</span>}
                  {...methods.register('amount')}
                  onKeyDown={(e) => { if (e.key === '-' || e.key === 'e') e.preventDefault(); }}
                  error={methods.formState.errors.amount?.message}
                />
              </div>

              <Input
                label="Description"
                autoComplete="off"
                disabled={methods.formState.isSubmitting}
                placeholder="e.g. Flight to New York Conference"
                {...methods.register('description')}
                error={methods.formState.errors.description?.message}
              />

              <div className="bg-slate-50/80 p-6 rounded-xl border border-slate-200 relative">
                <h3 className="text-xs font-bold text-slate-500 uppercase tracking-wider mb-4 flex items-center gap-2">
                  <span className="w-2 h-2 rounded-full bg-brand-500"></span>
                  {claimType} Specific Details
                </h3>
                
                <div className="relative z-10">
                  {claimType === CLAIM_TYPES.TRAVEL && <TravelFields />}
                  {claimType === CLAIM_TYPES.MEDICAL && <MedicalFields />}
                  {claimType === CLAIM_TYPES.ENTERTAINMENT && <p className="text-slate-500 italic text-sm">No additional receipts required for entertainment.</p>}
                </div>
              </div>

              <div className="flex items-center justify-end gap-3 pt-6 border-t border-slate-100">
                <Button 
                  type="button" 
                  variant="ghost" 
                  disabled={methods.formState.isSubmitting}
                  onClick={() => navigate('/dashboard')}
                >
                  Cancel
                </Button>
                <Button 
                  type="submit" 
                  isLoading={methods.formState.isSubmitting} 
                  className="px-8 shadow-brand-500/20 shadow-lg"
                >
                  Submit Request
                </Button>
              </div>

            </form>
          </FormProvider>
        </div>
      </div>
    </div>
  );
};

export default CreateClaimPage;