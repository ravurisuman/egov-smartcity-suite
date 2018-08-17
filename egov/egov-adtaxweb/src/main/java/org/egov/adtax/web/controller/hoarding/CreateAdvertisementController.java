/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */
package org.egov.adtax.web.controller.hoarding;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.adtax.entity.AdvertisementPermitDetail;
import org.egov.adtax.entity.SubCategory;
import org.egov.adtax.entity.enums.AdvertisementApplicationType;
import org.egov.adtax.entity.enums.AdvertisementStatus;
import org.egov.adtax.utils.constants.AdvertisementTaxConstants;
import org.egov.adtax.web.controller.common.HoardingControllerSupport;
import org.egov.adtax.workflow.AdvertisementWorkFlowService;
import org.egov.commons.entity.Source;
import org.egov.eis.entity.Assignment;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static org.egov.adtax.utils.constants.AdvertisementTaxConstants.ANONYMOUS_USER;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/hoarding")
public class CreateAdvertisementController extends HoardingControllerSupport {

    private static final String NOTEXISTS_POSITION = "notexists.position";
    private static final String CURRENT_STATE = "currentState";
    private static final String HOARDING_CREATE = "hoarding-create";
    private static final String ADDITIONAL_RULE = "additionalRule";
    private static final String STATE_TYPE = "stateType";
    private static final String IS_EMPLOYEE = "isEmployee";
    private static final String APPROVAL_POSITION = "approvalPosition";
    private static final String APPLICATION_PDF = "application/pdf";
    protected String reportId;
    @Autowired
    @Qualifier("messageSource")
    private MessageSource messageSource;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private AdvertisementWorkFlowService advertisementWorkFlowService;

    @RequestMapping(value = "child-boundaries", method = GET, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Boundary> childBoundaries(@RequestParam final Long parentBoundaryId) {
        return boundaryService.getActiveChildBoundariesByBoundaryId(parentBoundaryId);
    }

    @RequestMapping(value = "subcategories", method = GET, produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<SubCategory> hoardingSubcategories(@RequestParam final Long categoryId) {
        return subCategoryService.getAllActiveSubCategoryByCategoryId(categoryId);
    }

    @RequestMapping(value = { "/create" }, method = GET)
    public String createHoardingForm(@ModelAttribute final AdvertisementPermitDetail advertisementPermitDetail,
            final Model model, HttpServletRequest request) {
        User currentUser = securityUtils.getCurrentUser();

        buildCreateHoardingForm(advertisementPermitDetail, model);
        model.addAttribute(IS_EMPLOYEE, !ANONYMOUS_USER.equalsIgnoreCase(currentUser.getName())
                && advertisementWorkFlowService.isEmployee(securityUtils.getCurrentUser()));
        return HOARDING_CREATE;
    }

    @RequestMapping(value = { "/createbycitizen" }, method = GET)
    public String createHoardingByCitizen(@ModelAttribute final AdvertisementPermitDetail advertisementPermitDetail,
            final Model model, HttpServletRequest request) {

        buildCreateHoardingForm(advertisementPermitDetail, model);
        model.addAttribute("applicationSource", "online");

        return HOARDING_CREATE;
    }

    @RequestMapping(value = { "/create", "/createbycitizen" }, method = POST)
    public String createAdvertisement(@Valid @ModelAttribute final AdvertisementPermitDetail advertisementPermitDetail,
            final BindingResult resultBinder,
            final RedirectAttributes redirAttrib, final HttpServletRequest request, final Model model,
            @RequestParam String workFlowAction) {
        User currentuser = securityUtils.getCurrentUser();
        boolean isActiveApprover=false;
        Boolean isEmployee = !ANONYMOUS_USER.equalsIgnoreCase(currentuser.getName())
                && advertisementWorkFlowService.isEmployee(securityUtils.getCurrentUser());
        validateAssignmentForCscUser(advertisementPermitDetail, isEmployee, resultBinder);
        validateHoardingDocs(advertisementPermitDetail, resultBinder);
        validateAdvertisementDetails(advertisementPermitDetail, resultBinder);
        if (advertisementPermitDetail != null) {
            if (advertisementPermitDetail.getState() == null) {
                advertisementPermitDetail.setStatus(advertisementPermitDetailService
                        .getStatusByModuleAndCode(AdvertisementTaxConstants.APPLICATION_STATUS_CREATED));
            }
            advertisementPermitDetail.getAdvertisement().setStatus(AdvertisementStatus.WORKFLOW_IN_PROGRESS);

            advertisementPermitDetail.setSource(Source.SYSTEM.toString());
            advertisementPermitDetail.setApplicationtype(AdvertisementApplicationType.NEW);

            if (resultBinder.hasErrors()) {
                model.addAttribute(IS_EMPLOYEE,isEmployee);
                buildCreateHoardingForm(advertisementPermitDetail, model);
                return HOARDING_CREATE;
            }
        }
        storeHoardingDocuments(advertisementPermitDetail);

        Long approvalPosition = 0l;
        String approvalComment = "";
        String approverName = "";
        String nextDesignation = "";


        if (request.getParameter("approvalComent") != null)
            approvalComment = request.getParameter("approvalComent");
        if (request.getParameter("workFlowAction") != null)
            workFlowAction = request.getParameter("workFlowAction");
        if (request.getParameter("approverName") != null)
            approverName = request.getParameter("approverName");
        if (request.getParameter("nextDesignation") != null)
            nextDesignation = request.getParameter("nextDesignation");
        if (request.getParameter(APPROVAL_POSITION) != null && !request.getParameter(APPROVAL_POSITION).isEmpty())
            approvalPosition = Long.valueOf(request.getParameter(APPROVAL_POSITION));
        
        if (!isEmployee || ANONYMOUS_USER.equalsIgnoreCase(currentuser.getName())) {
            Assignment assignment = advertisementWorkFlowService.getMappedAssignmentForCscOperator(advertisementPermitDetail);
            if (assignment != null) {
                approvalPosition = assignment.getPosition().getId();
                approverName = assignment.getEmployee().getName();
                nextDesignation = assignment.getDesignation().getName();
                isActiveApprover =assignment.getToDate().compareTo(new Date())>=0;

            }
        }

        if (!isEmployee) {
            if (isActiveApprover) {
                return createAdvertisementOnApproverCheck(advertisementPermitDetail, redirAttrib, workFlowAction, currentuser,
                        isEmployee,
                        approvalPosition, approvalComment, approverName, nextDesignation);
            } else {
                model.addAttribute(IS_EMPLOYEE, isEmployee);
                model.addAttribute("message", NOTEXISTS_POSITION);
                buildCreateHoardingForm(advertisementPermitDetail, model);
                return HOARDING_CREATE;
            }

        }
        else{
            return createAdvertisementOnApproverCheck(advertisementPermitDetail, redirAttrib, workFlowAction, currentuser,
                    isEmployee, approvalPosition, approvalComment, approverName, nextDesignation); 
        }
    }

    private String createAdvertisementOnApproverCheck(final AdvertisementPermitDetail advertisementPermitDetail,
            final RedirectAttributes redirAttrib, String workFlowAction, User currentuser, Boolean isEmployee,
            Long approvalPosition, String approvalComment, String approverName, String nextDesignation) {
        advertisementPermitDetail.getAdvertisement().setPenaltyCalculationDate(advertisementPermitDetail.getApplicationDate());
        advertisementPermitDetailService.createAdvertisementPermitDetail(advertisementPermitDetail, approvalPosition,
                approvalComment, "CREATEADVERTISEMENT", workFlowAction, currentuser);
        redirAttrib.addFlashAttribute("advertisementPermitDetail", advertisementPermitDetail);
        String message = messageSource.getMessage("msg.success.forward",
                new String[] { approverName.concat("~").concat(nextDesignation),
                        advertisementPermitDetail.getApplicationNumber() },
                null);
        redirAttrib.addFlashAttribute("message", message);
        if (!isEmployee)
            return "redirect:/hoarding/showack/" + advertisementPermitDetail.getId();
         else 
            return "redirect:/hoarding/success/" + advertisementPermitDetail.getId();
    }
    
    @RequestMapping(value = "/success/{id}", method = GET)
    public ModelAndView successView(@PathVariable("id") final String id,
            @ModelAttribute final AdvertisementPermitDetail advertisementPermitDetail) {

        return new ModelAndView("hoarding/hoarding-success", "hoarding",
                advertisementPermitDetailService.findBy(Long.valueOf(id)));

    }

    @RequestMapping(value = "/showack/{id}", method = GET)
    public String showAck(@PathVariable Long id, final Model model) {
        AdvertisementPermitDetail advertisementPermitDetail = advertisementPermitDetailService.findBy(Long.valueOf(id));
        model.addAttribute("advertisementPermitDetail", advertisementPermitDetail);
        return "hoarding-ack";
    }

    @RequestMapping(value = "/printack/{id}", method = GET)
    @ResponseBody
    public ResponseEntity<byte[]> printAck(@PathVariable Long id, final Model model, final HttpServletRequest request) {
        byte[] reportOutput;
        final String cityMunicipalityName = (String) request.getSession()
                .getAttribute("citymunicipalityname");
        final String cityName = (String) request.getSession().getAttribute("cityname");
        AdvertisementPermitDetail advertisementPermitDetail = advertisementPermitDetailService.findBy(Long.valueOf(id));

        if (advertisementPermitDetail != null) {
            reportOutput = advertisementService
                    .getReportParamsForAcknowdgement(advertisementPermitDetail, cityMunicipalityName, cityName)
                    .getReportOutputData();
            if (reportOutput != null) {
                final HttpHeaders headers = new HttpHeaders();

                headers.setContentType(MediaType.parseMediaType(APPLICATION_PDF));
                headers.add("content-disposition", "inline;filename=hoarding-ack.pdf");
                return new ResponseEntity<>(reportOutput, headers, HttpStatus.CREATED);
            }
        }

        return null;

    }

    private void buildCreateHoardingForm(final AdvertisementPermitDetail advertisementPermitDetail, final Model model) {
        WorkflowContainer workFlowContainer = new WorkflowContainer();
        workFlowContainer.setAdditionalRule(AdvertisementTaxConstants.CREATE_ADDITIONAL_RULE);
        model.addAttribute(CURRENT_STATE, "NEW");
        prepareWorkflow(model, advertisementPermitDetail, workFlowContainer);
        model.addAttribute(ADDITIONAL_RULE, AdvertisementTaxConstants.CREATE_ADDITIONAL_RULE);
        model.addAttribute(STATE_TYPE, advertisementPermitDetail.getClass().getSimpleName());
    }
    
    public void validateAssignmentForCscUser(final AdvertisementPermitDetail advertisementPermitDetail, Boolean isEmployee,
            final BindingResult errors) {
        if (!isEmployee && advertisementPermitDetail != null) {
            final Assignment assignment = advertisementWorkFlowService.isCscOperator(securityUtils.getCurrentUser())
                    ? advertisementWorkFlowService.getAssignmentByDeptDesigElecWard(advertisementPermitDetail)
                    : null;
            if (assignment == null && advertisementWorkFlowService.getUserPositionByZone(advertisementPermitDetail) == null)
                errors.reject(NOTEXISTS_POSITION, NOTEXISTS_POSITION);
        }
    }

}
