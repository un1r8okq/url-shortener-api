import { useState } from 'react';
import { Button, Col, Row } from 'react-bootstrap';
import Form from 'react-bootstrap/Form';

export default function ShortenUrl({ shortenButtonDisabled, createShortUrl }) {
  const [longUrl, setLongUrl] = useState('');

  function updateLongUrl(event) {
    setLongUrl(event.target.value);
  }

  return (
    <>
      <h2 className="mb-3">Enter something long... we'll make it short!</h2>
      <Form>
        <Row>
          <Col className="p-1">
            <Form.Control
              type="url"
              placeholder="Enter a long URL..."
              value={longUrl}
              onChange={updateLongUrl}
              className="mb-3 w-auto"
              autoFocus
            />
          </Col>
          <Col className="p-1">
            <Button
              variant="primary"
              type="submit"
              onClick={() => createShortUrl(longUrl)}
              disabled={shortenButtonDisabled}>
              {shortenButtonDisabled ? 'Shortening...' : 'Make it short!'}
            </Button>
          </Col>
        </Row>
      </Form>
    </>
  );
}
