import axios from 'axios';
import { useEffect, useState } from 'react';
import { Alert, ListGroup, ListGroupItem, Spinner } from 'react-bootstrap';

export default function AllUrls() {
  const [error, setError] = useState('');
  const [isLoading, setLoading] = useState(true);
  const [pageNumber, setPageNumber] = useState(1);
  const [urls, setUrls] = useState([]);

  useEffect(() => {
    const fetchUrls = async () => {
      setLoading(true);
      setError('');
      setUrls([]);
      try {
        const response = await axios.get(
          `/api/v1/urls?pageNumber=${pageNumber}`,
        );
        setUrls(response.data.data);
        setLoading(false);
      } catch (error) {
        console.error(error);
        setError(
          'Something went wrong when fetching the list of URLs. Please try again later.',
        );
      }
    };

    fetchUrls();
  }, [pageNumber]);

  let result = null;

  if (error !== '') {
    result = <Alert variant="danger">{error}</Alert>;
  } else if (isLoading) {
    result = (
      <div className="flex-grow-1 d-flex justify-content-center align-items-center">
        <Spinner className="m-2" />
        <h3>Loading URLs</h3>
      </div>
    );
  } else if (!isLoading) {
    result = (
      <>
        <ListGroup>
          {urls.map((url) => (
            <ListGroupItem key={url.shortenedUrl}>{url.longUrl}</ListGroupItem>
          ))}
        </ListGroup>
      </>
    );
  }

  return <div className="h-100 m-5 d-flex flex-column">{result}</div>;
}
