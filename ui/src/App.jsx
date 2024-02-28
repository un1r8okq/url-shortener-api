import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';
import { Container, Nav, Navbar } from 'react-bootstrap';
import { Outlet } from 'react-router-dom';
import { LinkContainer } from 'react-router-bootstrap';

function App() {
  return (
    <div id="app" className="d-flex flex-column justify-content-between">
      <Navbar className="border-bottom">
        <Container>
          <Navbar.Brand>URL Shortener</Navbar.Brand>
          <Nav>
            <LinkContainer to="/">
              <Nav.Link>Shorten URL</Nav.Link>
            </LinkContainer>
            <LinkContainer to="/all-urls">
              <Nav.Link>All URLs</Nav.Link>
            </LinkContainer>
          </Nav>
        </Container>
      </Navbar>
      <div id="app" className="d-flex flex-column justify-content-between">
        <div className="m-3 h-100 d-flex align-items-center justify-content-center">
          <Outlet />
        </div>
      </div>
    </div>
  );
}

export default App;
