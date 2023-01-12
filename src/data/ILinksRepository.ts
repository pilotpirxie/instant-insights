import Links from '../domain/links';

export type AddLinkHit = {
  name: string,
  meta: { [p: string]: string },
  params: { [p: string]: string },
};

export interface ILinksRepository {
  getLinks(name: string): Promise<Links | null>;
  addLinkHit(data: AddLinkHit): Promise<void>;
}
