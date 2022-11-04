export type Event = {
  appId: number,
  type: string,
  meta: { [p: string]: string },
  params: { [p: string]: string },
  createdAt: string,
}
