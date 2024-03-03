import { Button } from 'react-bootstrap';

export default function ShortUrlResult({ shortenedUrl, resetForm }) {
  return (
    <>
      <h2 className="text-center">
        Your short URL is <a href={shortenedUrl}>{shortenedUrl}</a>
      </h2>
      <Button onClick={resetForm} variant="link" className="mt-3">
        ...shorten another URL
      </Button>
    </>
  );
}
