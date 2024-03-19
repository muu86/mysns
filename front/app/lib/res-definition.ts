export type GetUserResponse = {
  username: string;
};

export type GetFeedsResponse = {
  postId: number;
  version: number;
  user: {
    userId: number;
    username: string;
    first: string;
    last: string;
    email: string;
    oauth2: boolean;
    provider: string | null;
  };
  content: string;
  files: [];
  legalAddress: {
    id: number;
    code: string;
    sido: string;
    gungu: string;
    eupmyundong: string;
    li: string;
    sunwi: string;
    createdAt: string;
    modifiedAt: string | null;
    deletedAt: string | null;
    prevCode: string | null;
  };
  createdAt: string;
  modifiedAt: string | null;
  deletedAt: string | null;
};
