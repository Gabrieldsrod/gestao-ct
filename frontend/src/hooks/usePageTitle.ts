import { useEffect } from 'react';

export function usePageTitle(title: string) {
  useEffect(() => {
    document.title = `${title} | Gestão CT`;
    return () => {
      document.title = 'Gestão CT';
    };
  }, [title]);
}