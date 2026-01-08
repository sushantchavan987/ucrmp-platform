import { motion } from 'framer-motion';
import { type ClaimResponse } from '../../types/claim';
import { formatDate, formatCurrency } from '../../lib/utils';
import { StatusBadge } from '../ui/StatusBadge';
import { Inbox, Plane, Stethoscope, PartyPopper } from 'lucide-react';
import { Link } from 'react-router-dom';
import { Button } from '../ui/Button';
// ✅ FIX: Corrected path from 'lib' to 'utils'
import { CLAIM_TYPES } from '../../utils/constants'; 

interface ClaimsTableProps {
  claims: ClaimResponse[];
}

export const ClaimsTable: React.FC<ClaimsTableProps> = ({ claims }) => {
  
  const getTypeIcon = (type: string) => {
    switch (type) {
      case CLAIM_TYPES.TRAVEL: return <Plane size={14} />;
      case CLAIM_TYPES.MEDICAL: return <Stethoscope size={14} />;
      case CLAIM_TYPES.ENTERTAINMENT: return <PartyPopper size={14} />;
      default: return null;
    }
  };

  if (claims.length === 0) {
    return (
        <div className="bg-white rounded-2xl border border-dashed border-slate-300 p-12 text-center flex flex-col items-center justify-center h-64">
            <div className="w-14 h-14 bg-slate-50 rounded-full flex items-center justify-center text-slate-400 mb-4">
                <Inbox size={28} />
            </div>
            <h3 className="text-base font-semibold text-slate-900">No claims yet</h3>
            <p className="text-slate-500 text-sm mb-6 max-w-xs mx-auto">
                Create your first claim to start tracking your reimbursements.
            </p>
            <Link to="/create-claim">
                <Button variant="outline">Create Claim</Button>
            </Link>
        </div>
    );
  }

  return (
    <div className="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden">
        <div className="overflow-x-auto">
            <table className="w-full text-left text-sm">
                <thead className="bg-slate-50/80 border-b border-slate-200">
                    <tr>
                        <th className="hidden md:table-cell px-6 py-4 font-semibold text-slate-500">Date</th>
                        <th className="px-6 py-4 font-semibold text-slate-500">Description</th>
                        <th className="hidden md:table-cell px-6 py-4 font-semibold text-slate-500">Type</th>
                        <th className="px-6 py-4 font-semibold text-slate-500 text-right">Amount</th>
                        <th className="px-6 py-4 font-semibold text-slate-500 text-right">Status</th>
                    </tr>
                </thead>
                
                <motion.tbody 
                  className="divide-y divide-slate-100"
                  initial="hidden"
                  animate="show"
                  variants={{ show: { transition: { staggerChildren: 0.05 } } }}
                >
                    {claims.map((claim) => (
                        <motion.tr 
                          key={claim.id} 
                          title={`Claim ID: ${claim.id}`}
                          className="hover:bg-slate-50 transition-colors group cursor-default"
                          variants={{ hidden: { opacity: 0, y: 10 }, show: { opacity: 1, y: 0 } }}
                        >
                            {/* Date */}
                            <td className="hidden md:table-cell px-6 py-4 text-slate-500 whitespace-nowrap">
                                {formatDate(claim.createdDate || claim.createdAt || '')} 
                            </td>
                            
                            {/* Description & Mobile Metadata */}
                            <td className="px-6 py-4 font-medium text-slate-900">
                                <div className="truncate max-w-[150px] sm:max-w-[300px]" title={claim.description}>
                                    {claim.description || "General Expense"}
                                </div>
                                {/* Mobile Date + Type */}
                                <div className="md:hidden flex items-center gap-2 text-xs text-slate-400 mt-1">
                                    <span>{formatDate(claim.createdDate || claim.createdAt || '')}</span>
                                    <span className="w-1 h-1 rounded-full bg-slate-300"></span>
                                    <span className="flex items-center gap-1 text-brand-600">
                                        {getTypeIcon(claim.claimType)} {claim.claimType}
                                    </span>
                                </div>
                            </td>
                            
                            {/* Type (Desktop) */}
                            <td className="hidden md:table-cell px-6 py-4">
                                <span className="inline-flex items-center px-2.5 py-0.5 rounded-md text-xs font-medium bg-slate-100 text-slate-700 border border-slate-200 uppercase tracking-wide">
                                    {claim.claimType}
                                </span>
                            </td>
                            
                            {/* Amount */}
                            <td className="px-6 py-4 text-right font-mono font-medium text-slate-900">
                                {formatCurrency(claim.amount)}
                            </td>
                            
                            {/* Status */}
                            <td className="px-6 py-4 text-right">
                                <StatusBadge status={claim.status || 'PENDING'} />
                            </td>
                        </motion.tr>
                    ))}
                </motion.tbody>
            </table>
        </div>
    </div>
  );
};