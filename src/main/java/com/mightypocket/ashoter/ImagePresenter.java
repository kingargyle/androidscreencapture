/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mightypocket.ashoter;

import java.awt.Dimension;
import java.awt.Image;

/**
 *
 * @author etf
 */
public interface ImagePresenter {
    void setImage(Image img);
    Dimension getPresenterDimension();
    void copy();
}
