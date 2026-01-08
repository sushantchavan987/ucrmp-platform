import { useEffect } from 'react';
import { useLocation } from 'react-router-dom';

export const ScrollToTop = () => {
  const { pathname } = useLocation();

  useEffect(() => {
    // 1. Visual Scroll Reset
    window.scrollTo(0, 0);
    
    // 2. ✅ A11Y FIX: Focus Reset
    // Moves keyboard focus to the top of the document so screen readers 
    // start reading from the top of the new page, not the bottom of the old one.
    document.body.focus(); 
  }, [pathname]);

  return null;
};