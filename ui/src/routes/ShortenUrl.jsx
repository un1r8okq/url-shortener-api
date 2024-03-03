import 'bootstrap/dist/css/bootstrap.min.css';
import axios from 'axios';
import { useState } from 'react';
import CreateShortUrl from '../components/CreateShortUrl';
import ShortUrlResult from '../components/ShortUrlResult';
import { Alert } from 'react-bootstrap';

export default function ShortenUrl() {
  const [shortenButtonDisabled, setShortenButtonDisabled] = useState(false);
  const [shortenedUrl, setShortenedUrl] = useState('');
  const [error, setError] = useState('');

  async function createShortUrl(longUrl) {
    setShortenButtonDisabled(true);

    try {
      const result = await axios.post('/api/v1/urls', { longUrl });
      if (result.status === 201) {
        setShortenedUrl(result.data.shortenedUrl);
        return;
      }

      throw new Error(
        'Something went wrong when submitting an URL to be shortened. Please try again later.',
      );
    } catch (error) {
      console.error(error);

      if (error.code === 'ERR_NETWORK') {
        setError('Unable to contact server. Please try again later.');
      } else if (error.code === 'ERR_BAD_RESPONSE') {
        setError(
          'The server was unable to shorten the URL. Please try again later.',
        );
      } else {
        setError(error.message);
      }
    } finally {
      setShortenButtonDisabled(false);
    }
  }

  function resetForm() {
    setShortenButtonDisabled(false);
    setError('');
    setShortenedUrl('');
  }

  const shortenUrlForm = (
    <CreateShortUrl
      shortenButtonDisabled={shortenButtonDisabled}
      createShortUrl={createShortUrl}
    />
  );
  const shortenUrlResult = (
    <ShortUrlResult resetForm={resetForm} shortenedUrl={shortenedUrl} />
  );

  return (
    <div className="d-flex align-items-center justify-content-center flex-grow-1 m-3">
      <div className="d-flex flex-column justify-content-center align-items-center">
        {shortenedUrl === '' ? shortenUrlForm : shortenUrlResult}
        {error && (
          <Alert className="w-50" variant="danger">
            {error}
          </Alert>
        )}
      </div>
    </div>
  );
}
