type RedirectLink = {
  id: string,
  name: string,
  links: { [p: string]: string },
  createdAt: string,
}

export default RedirectLink;
