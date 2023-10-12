export type EventEntity = {
  pathname: string,
  fingerprint: string,
  type: string,
  meta: { [p: string]: string },
  params: { [p: string]: string },
  created_at?: string,
}

export type LinkHitEntity = {
  name: string,
  meta: { [p: string]: string },
  params: { [p: string]: string },
  created_at?: string,
}
