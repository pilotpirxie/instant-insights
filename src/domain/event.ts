export type Event = {
  appId: number,
  type: string,
  pathname: string,
  fingerprint: string,
  meta: { [p: string]: string },
  params: { [p: string]: string },
  createdAt: string,
}
