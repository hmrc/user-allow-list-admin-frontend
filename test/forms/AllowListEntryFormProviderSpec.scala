/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package forms

import models.AllowListEntry
import org.scalatest.OptionValues
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers

class AllowListEntryFormProviderSpec extends AnyFreeSpec with Matchers with OptionValues {

  private val form = new AllowListEntryFormProvider()()

  "must bind when given valid data" in {

    val data = Map(
      "feature" -> "foobar",
      "value" -> "baz"
    )

    val expected = AllowListEntry("foobar", "baz")

    val boundForm = form.bind(data)

    boundForm.errors mustBe empty
    boundForm.value.value mustEqual expected
  }

  "must fail to bind when the feature is missing" in {

    val boundForm = form.bind(Map.empty[String, String])
    val field = boundForm("feature")

    field.errors.length mustBe 1

    val error = field.error.value

    error.message mustEqual "error.required"
    error.key mustEqual "feature"
  }

  "must fail when feature is blank" in {

    val boundForm = form.bind(Map("feature" -> "  "))
    val field = boundForm("feature")

    field.errors.length mustBe 1

    val error = field.error.value

    error.message mustEqual "error.required"
    error.key mustEqual "feature"
  }

  "must fail to bind when the value is missing" in {

    val boundForm = form.bind(Map.empty[String, String])
    val field = boundForm("value")

    field.errors.length mustBe 1

    val error = field.error.value

    error.message mustEqual "error.required"
    error.key mustEqual "value"
  }

  "must fail when value is blank" in {

    val boundForm = form.bind(Map("value" -> "  "))
    val field = boundForm("value")

    field.errors.length mustBe 1

    val error = field.error.value

    error.message mustEqual "error.required"
    error.key mustEqual "value"
  }
}
