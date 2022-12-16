export interface Maintenance {
  migrate(dir: string): Promise<void>;
  backup(): Promise<void>;
}
