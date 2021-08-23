package utils

import play.api.http.HttpFilters
import play.api.mvc.EssentialFilter
import play.filters.csrf.CSRFFilter
import play.filters.headers.SecurityHeadersFilter

import javax.inject.Inject

/**
 * Provides filters.
 */
class Filters @Inject() (csrfFilter: CSRFFilter, securityHeadersFilter: SecurityHeadersFilter) extends HttpFilters {
  override def filters: Seq[EssentialFilter] = Seq(csrfFilter, securityHeadersFilter)
}
