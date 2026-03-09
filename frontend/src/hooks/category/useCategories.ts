import { useState, useEffect, useCallback } from 'react';
import type { Category } from '@/types/finances/Category';

export function useCategories() {
    const [categories, setCategories] = useState<Category[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const API_URL = import.meta.env.VITE_API_URL;

    const fetchCategories = useCallback(async () => {
        setIsLoading(true);
        setError(null);
        try {
            const response = await fetch(`${API_URL}/v1/api/categories`);
            if (!response.ok) throw new Error('Erro ao buscar categorias.');

            const data = await response.json();
            setCategories(data);
        } catch (err: any) {
            setError(err.message || 'Erro de conexão');
        } finally {
            setIsLoading(false);
        }
    }, [API_URL]);

    useEffect(() => {
        fetchCategories();
    }, [fetchCategories]);

    const createCategory = async (name: string, type: 'INCOME' | 'EXPENSE') => {
        try {
            const response = await fetch(`${API_URL}/v1/api/categories`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name, type }),
            });

            if (!response.ok) throw new Error('Falha ao criar categoria.');

            await fetchCategories();
            return { success: true };
        } catch (err: any) {
            return { success: false, message: err.message };
        }
    };

    return { categories, isLoading, error, createCategory };
}