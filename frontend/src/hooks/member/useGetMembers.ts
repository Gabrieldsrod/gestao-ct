import { useState, useEffect } from 'react';
import type { Member } from '@/types/member/Member';

export function useGetMembers(page = 0, size = 10, searchTerm = '', status = 'ACTIVE') {
  const [members, setMembers] = useState<Member[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const controller = new AbortController();

    async function fetchMembers() {
      try {
        setIsLoading(true);
        setError(null);

        const baseUrl = searchTerm
          ? `${import.meta.env.VITE_API_URL}/v1/api/members/search`
          : `${import.meta.env.VITE_API_URL}/v1/api/members`;

        const url = new URL(baseUrl);
        url.searchParams.append('page', page.toString());
        url.searchParams.append('size', size.toString());

        if (searchTerm) {
          url.searchParams.append('name', searchTerm);
        }

        if (status && status !== 'ALL') {
          url.searchParams.append('status', status);
        }

        const response = await fetch(url.toString(), {
          signal: controller.signal,
          headers: { 'Content-Type': 'application/json' },
        });

        if (!response.ok) throw new Error(`Erro ao buscar alunos: ${response.status}`);

        const data = await response.json();

        if (data && Array.isArray(data.content)) {
          setMembers(data.content);
          setTotalPages(data.page?.totalPages || 0);
          setTotalElements(data.page?.totalElements || 0);
        } else if (Array.isArray(data)) {
          setMembers(data);
        } else {
          setMembers([]);
        }

      } catch (err: unknown) {
        if (err instanceof Error && err.name !== 'AbortError') {
          setError(err.message || 'Erro de conexão');
        } else if (!(err instanceof Error) || err.name !== 'AbortError') {
          setError('Erro de conexão');
        }
      } finally {
        setIsLoading(false);
      }
    }

    fetchMembers();

    return () => controller.abort();
  }, [page, size, searchTerm, status]);

  return { members, totalPages, totalElements, isLoading, error };
}

export function useSearchMembers(searchTerm: string) {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const [suggestions, setSuggestions] = useState<any[]>([]);
  const [isSearching, setIsSearching] = useState(false);

  useEffect(() => {
    if (!searchTerm) {
      setSuggestions([]);
      return;
    }

    const controller = new AbortController();

    async function fetchSuggestions() {
      setIsSearching(true);
      try {
        const url = `${import.meta.env.VITE_API_URL}/v1/api/members/search?name=${searchTerm}&size=10`;
        const res = await fetch(url, { signal: controller.signal });
        
        if (res.ok) {
          const data = await res.json();
          setSuggestions(data.content || data);
        }
      } catch (err) {
        if (err instanceof Error && err.name !== 'AbortError') {
          console.error("Erro ao buscar sugestões de alunos", err);
        }
      } finally {
        setIsSearching(false);
      }
    }

    fetchSuggestions();
    return () => controller.abort();
  }, [searchTerm]);

  return { suggestions, setSuggestions, isSearching };
}