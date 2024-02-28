import { Button } from 'react-bootstrap';

export default function ShortenedUrlResult({ shortenedUrl, resetForm }) {
  return (
    <>
      <h2>Here's your short URL:</h2>
      <p className="mb-5">
        <a href={shortenedUrl}>{shortenedUrl}</a>
      </p>
      <Button onClick={resetForm} variant="link">
        ...shorten another URL
      </Button>
    </>
  );
}
