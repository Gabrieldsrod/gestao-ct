export interface MemberResponse {
    name: string;
    whasapp: string;
    email: string;
    nomePlano: string;
    status: string;
    
}

export interface Page<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
}