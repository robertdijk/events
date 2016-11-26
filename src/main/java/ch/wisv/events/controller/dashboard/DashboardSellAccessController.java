package ch.wisv.events.controller.dashboard;

import ch.wisv.events.service.sales.SellAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Copyright (c) 2016  W.I.S.V. 'Christiaan Huygens'
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
@Controller
@RequestMapping("/dashboard/sell_access")
@PreAuthorize("hasRole('ADMIN')")
public class DashboardSellAccessController {


    private final SellAccessService sellAccessService;

    /**
     * Default constructor
     *
     * @param sellAccessService ProductService
     */
    @Autowired
    public DashboardSellAccessController(SellAccessService sellAccessService) {
        this.sellAccessService = sellAccessService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("sellers", sellAccessService.getAllSellAccess());

        return "dashboard/sell_access/index";
    }
}
