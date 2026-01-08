import { useEffect } from 'react';

export const useTitle = (title: string) => {
  useEffect(() => {
    const prevTitle = document.title;
    document.title = `${title} | UCRMP`;

    // Cleanup: Revert title when leaving (optional, but good practice)
    return () => {
      document.title = prevTitle;
    };
  }, [title]);
};