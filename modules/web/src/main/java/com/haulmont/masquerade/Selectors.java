/*
 * Copyright (c) 2008-2017 Haulmont.
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

package com.haulmont.masquerade;

import com.codeborne.selenide.SelenideElement;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.By.ByClassName;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ByChained;

import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.haulmont.masquerade.Components.wire;
import static java.util.Collections.singletonList;

public class Selectors extends com.codeborne.selenide.Selectors {
    protected Selectors() {
    }

    public static By byPath(String... path) {
        checkNotNull(path);

        if (path.length == 1) {
            return byCubaId(path[0]);
        }

        By[] bys = new By[path.length];

        for (int i = 0; i < path.length; i++) {
            bys[i] = byCubaId(path[i]);
        }

        return byChain(bys);
    }

    public static By byCubaId(String cubaId) {
        checkNotNull(cubaId);

        return new ByCubaId(cubaId);
    }

    public static By byChain(By... bys) {
        checkNotNull(bys);

        return new ByChain(bys);
    }

    public static By byTarget(SelenideElement target) {
        return new ByTarget(target);
    }

    public static By byCells(String... cellValues) {
        return new ByCells(cellValues);
    }

    public static By byIndex(int index) {
        return new ByIndex(index);
    }

    public static By byRowIndex(int index) {
        return new ByRowIndex(index);
    }

    public static By byRowColIndexes(int rowIndex, int colIndex) {
        return new ByRowColIndexes(rowIndex, colIndex);
    }

    /**
     * Find element that has given text (the whole text, not a substring).
     *
     * This method ignores difference between space, \n, \r, \t and &nbsp;
     * This method ignores multiple spaces.
     *
     * @param cellText Text that searched element should have
     * @return standard selenium By criteria
     */
    public static By byText(String cellText) {
        return new ByTargetText(cellText);
    }

    /**
     * Find element CONTAINING given text (as a substring).
     *
     * This method ignores difference between space, \n, \r, \t and &nbsp;
     * This method ignores multiple spaces.
     *
     * @param cellText Text to search inside element
     * @return standard selenium By criteria`
     */
    public static By withText(String cellText) {
        return new WithTargetText(cellText);
    }

    public static By byClassName(String className) {
        return new ByTargetClassName(className);
    }

    public static By isSelected() {
        return new BySelected();
    }

    public static By isVisible() {
        return new ByVisibleRows();
    }

    /**
     * Get selenide element by cuba-id.
     *
     * @param cubaId cuba-id value
     * @return SelenideElement
     */
    public static SelenideElement $c(String cubaId) {
        return $(byCubaId(cubaId));
    }

    /**
     * Get selenide element by cuba-id path.
     *
     * @param path cuba-id path
     * @return SelenideElement
     */
    public static SelenideElement $c(String... path) {
        return $(byPath(path));
    }

    public static <T> T $c(Class<T> clazz) {
        return wire(clazz);
    }

    public static <T> T $c(Class<T> clazz, String... path) {
        return wire(clazz, path);
    }

    public static <T> T $c(Class<T> clazz, By by) {
        return wire(clazz, by);
    }

    public static <T> T $c(Class<T> clazz, SelenideElement target) {
        return wire(clazz, target);
    }

    public static class ByTarget extends By {
        private final SelenideElement target;

        public ByTarget(SelenideElement target) {
            this.target = target;
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            return singletonList(target.getWrappedElement());
        }

        @Override
        public String toString() {
            return "By.target: " + target;
        }
    }

    public static class ByCubaId extends By.ByCssSelector {
        private final String cubaId;

        public ByCubaId(String cubaId) {
            super(String.format("[cuba-id='%s']", cubaId));

            this.cubaId = cubaId;
        }

        public String getCubaId() {
            return cubaId;
        }

        @Override
        public String toString() {
            return "By.cubaId: " + cubaId;
        }
    }

    public static class ByChain extends ByChained {
        private By[] bys;

        public ByChain(By... bys) {
            super(bys);
            this.bys = bys;
        }

        public By[] getBys() {
            return bys;
        }

        public By getLastBy() {
            return bys[bys.length - 1];
        }
    }

    public static class ByIndex extends By {
        protected final int index;

        public ByIndex(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            throw new RuntimeException(
                    "ByIndex must be checked ony in Component implementations");
        }

        @Override
        public String toString() {
            return "By.index: " + index;
        }
    }

    public static class ByRowIndex extends ByIndex {
        public ByRowIndex(int index) {
            super(index);
        }

        @Override
        public String toString() {
            return "By.rowIndex: " + index;
        }
    }

    public static class ByCells extends By {
        private final String[] cellValues;

        public ByCells(String[] cellValues) {
            this.cellValues = cellValues;
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            throw new RuntimeException(
                    "ByCells must be checked ony in Table implementations");
        }

        public String[] getCellValues() {
            return cellValues;
        }

        @Override
        public String toString() {
            return "By.cells: " + StringUtils.join(cellValues, ',');
        }
    }

    public static class ByTargetText extends com.codeborne.selenide.Selectors.ByText {
        public ByTargetText(String elementText) {
            super(elementText);
        }

        public String getElementText() {
            return elementText;
        }
    }

    public static class WithTargetText extends com.codeborne.selenide.Selectors.WithText {
        public WithTargetText(String elementText) {
            super(elementText);
        }

        public String getElementText() {
            return elementText;
        }
    }

    public static class ByTargetClassName extends ByClassName {

        protected String expectedClassName;

        public ByTargetClassName(String className) {
            super(className);
        }

        public String getExpectedClassName() {
            return expectedClassName;
        }
    }

    public static class BySelected extends By {
        public BySelected() {
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            throw new RuntimeException(
                    "BySelected must be checked ony in Component implementations");
        }

        @Override
        public String toString() {
            return "By.selectedRow";
        }
    }

    public static class ByVisibleRows extends By {
        public ByVisibleRows() {
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            throw new RuntimeException(
                    "ByVisibleRows must be checked ony in Component implementations");
        }

        @Override
        public String toString() {
            return "By.visibleRows";
        }
    }

    public static class ByRowColIndexes extends By {
        private int rowIndex;
        private int colIndex;

        public ByRowColIndexes(int rowIndex, int colIndex) {
            this.rowIndex = rowIndex;
            this.colIndex = colIndex;
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            throw new RuntimeException(
                    "ByRowColIndexes must be checked ony in Component implementations");
        }

        @Override
        public String toString() {
            return "By.colRowIndexes";
        }

        public int getRowIndex() {
            return rowIndex;
        }

        public int getColIndex() {
            return colIndex;
        }
    }
}