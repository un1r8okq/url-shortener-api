import { useState } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import Form from 'react-bootstrap/Form';

export default function CreateShortUrl({
  shortenButtonDisabled: submitButtonDisabled,
  createShortUrl,
}) {
  const [url, setUrl] = useState('');

  function handleSubmit(event) {
    event.preventDefault();
    createShortUrl(url);
  }

  return (
    <>
      <h2 className="mb-4">Enter a long URL... we'll make it short!</h2>
      <Form onSubmit={handleSubmit}>
        <Row>
          <Col>
            <Form.Control
              type="url"
              pattern="https?://.+\..+"
              placeholder="Enter a long URL..."
              title="The URL must be valid and start with http:// or https://"
              value={url}
              onChange={(e) => setUrl(e.target.value)}
              className="mb-1 w-auto"
              autoFocus
              required
            />
          </Col>
          <Col>
            <Button
              variant="primary"
              type="submit"
              disabled={submitButtonDisabled}>
              {submitButtonDisabled ? 'Shortening...' : 'Shorten'}
            </Button>
          </Col>
        </Row>
      </Form>
    </>
  );
}
