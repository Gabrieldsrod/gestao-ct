import { useState, useEffect } from 'react';
import type { Member } from '../types/Member';

// O hook agora aceita um terceiro parâmetro: o searchTerm
export function useMembers(page = 0, size = 10, searchTerm = '') {
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
        
        // MÁGICA: Se tiver texto, usa o endpoint de pesquisa. Se não, usa o de listar todos.
        const baseUrl = searchTerm 
          ? `${import.meta.env.VITE_API_URL}/v1/api/members/search`
          : `${import.meta.env.VITE_API_URL}/v1/api/members`;

        const url = new URL(baseUrl);
        url.searchParams.append('page', page.toString());
        url.searchParams.append('size', size.toString());
        
        // Se for pesquisa, adiciona o parâmetro. 
        // ATENÇÃO: Verifique no seu Swagger se a sua API espera '?name=', '?nome=' ou '?keyword='
        if (searchTerm) {
          url.searchParams.append('name', searchTerm); 
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

      } catch (err: any) {
        if (err.name !== 'AbortError') setError(err.message || 'Erro de conexão');
      } finally {
        setIsLoading(false);
      }
    }

    fetchMembers();

    return () => controller.abort();
  }, [page, size, searchTerm]); // Adicionamos o searchTerm aqui para refazer o fetch se ele mudar!

  return { members, totalPages, totalElements, isLoading, error };
}