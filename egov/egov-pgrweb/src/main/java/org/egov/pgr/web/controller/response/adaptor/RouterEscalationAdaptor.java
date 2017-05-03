/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *     accountability and the service delivery of the government  organizations.
 *
 *      Copyright (C) 2016  eGovernments Foundation
 *
 *      The updated version of eGov suite of products as by eGovernments Foundation
 *      is available at http://www.egovernments.org
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU General Public License as published by
 *      the Free Software Foundation, either version 3 of the License, or
 *      any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU General Public License for more details.
 *
 *      You should have received a copy of the GNU General Public License
 *      along with this program. If not, see http://www.gnu.org/licenses/ or
 *      http://www.gnu.org/licenses/gpl.html .
 *
 *      In addition to the terms of the GPL license to be adhered to in using this
 *      program, the following additional terms are to be complied with:
 *
 *          1) All versions of this program, verbatim or modified must carry this
 *             Legal Notice.
 *
 *          2) Any misrepresentation of the origin of the material is prohibited. It
 *             is required that all modified versions of this material be marked in
 *             reasonable ways as different from the original version.
 *
 *          3) This license does not grant any rights to any user of the program
 *             with regards to rights under trademark law for use of the trade names
 *             or trademarks of eGovernments Foundation.
 *
 *    In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.pgr.web.controller.response.adaptor;

import java.lang.reflect.Type;
import java.util.List;

import org.egov.infra.web.support.json.adapter.DataTableJsonAdapter;
import org.egov.infra.web.support.ui.DataTable;
import org.egov.pgr.entity.dto.RouterEscalationForm;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public class RouterEscalationAdaptor implements DataTableJsonAdapter<RouterEscalationForm> {
    @Override
    public JsonElement serialize(final DataTable<RouterEscalationForm> routerEscalationForm, final Type type,
            final JsonSerializationContext jsc) {
        final List<RouterEscalationForm> routerEscalationReport = routerEscalationForm.getData();
        final JsonArray routerEscalationReportData = new JsonArray();
        routerEscalationReport.forEach(routerEscalation -> {
            final JsonObject routerEscalationResponse = new JsonObject();
            routerEscalationResponse.addProperty("complainttype", routerEscalation.getCtname());
            routerEscalationResponse.addProperty("ward", routerEscalation.getBndryname());
            routerEscalationResponse.addProperty("routedto", routerEscalation.getRouterposname());
            routerEscalationResponse.addProperty("firstescpos", routerEscalation.getEsclvl1posname());
            routerEscalationResponse.addProperty("secondescpos", routerEscalation.getEsclvl2posname());
            routerEscalationResponse.addProperty("thirdescpos", routerEscalation.getEsclvl3posname());

            routerEscalationReportData.add(routerEscalationResponse);
        });
        return enhance(routerEscalationReportData, routerEscalationForm);
    }
}
