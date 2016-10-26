package controllers;

import authentication.AuthManager;
import authentication.AuthRequirement;
import models.Event;
import models.Registrant;
import models.User;
import models.dao.EventDAO;
import models.dao.RegistrantDAO;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Alexis on 26/10/2016.
 */
public class EventController implements PageController {
    @Override
    public AuthRequirementState getAuthRequirementState() {
        return AuthRequirementState.IS_NOT_REQUIRED;
    }

    @Override
    public void getExecute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User currentUser = AuthManager.getInstance(request.getSession()).getCurrentUser();
        Event event = this.retrieveEventWithUri(request);
        boolean isManageable = currentUser.getMail().equals(event.getUser().getMail());
        request.getSession().setAttribute("event", event);
        request.getSession().setAttribute("registrants", this.retrieveEventRegistrants(event));
        request.getSession().setAttribute("isManageable", isManageable);
        request.getRequestDispatcher("/partials/event.jsp").forward(request, response);
    }

    @Override
    public void postExecute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    private Event retrieveEventWithUri(HttpServletRequest request){
        String uri = request.getRequestURI();
        String splittedUri[] = uri.split("/");
        int eventId = Integer.parseInt(splittedUri[splittedUri.length-1]);
        EventDAO eDAO = new EventDAO();
        return eDAO.findEventById(eventId);
    }

    private ArrayList<Registrant> retrieveEventRegistrants(Event event) {
        RegistrantDAO rDAO = new RegistrantDAO();
        return rDAO.findRegistrantsByEvent(event);
    }
}