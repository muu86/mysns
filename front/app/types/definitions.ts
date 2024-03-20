export type Post = {
  username: string;
  content: string;
  legalAddress: LegalAddress;
  createdAt: string;
  modifiedAt: string | null;
};

export type LegalAddress = {
  code: string;
  sido: string;
  gungu: string;
  eupmyundong: string;
  li: string;
};
