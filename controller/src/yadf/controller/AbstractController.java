/**
 * yadf
 * 
 * https://sourceforge.net/projects/yadf
 * 
 * Ben Smith (bensmith87@gmail.com)
 * 
 * yadf is placed under the BSD license.
 * 
 * Copyright (c) 2012-2013, Ben Smith All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 * 
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * - Neither the name of the yadf project nor the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package yadf.controller;

import java.util.ArrayList;
import java.util.List;

import yadf.controller.command.AbstractCommand;
import yadf.simulation.Region;

/**
 * Abstract base class for the controller, basically every command that changes the region must go through the
 * controller.
 * TODO: make this injected
 * @author Ben Smith (bensmith87@gmail.com)
 */
public abstract class AbstractController {
    
    /** A vector of the local commands, i.e. commands that apply to the local region/player */
    protected List<AbstractCommand> localCommands = new ArrayList<>();

    /**
     * Adds a command to the local command vector.
     * 
     * @param command the command
     */
    public synchronized void addCommand(final AbstractCommand command) {
        localCommands.add(command);
    }

    /**
     * Close the controller, the implementation should use this to close all connections etc...
     * 
     * @throws Exception the exception
     */
    public abstract void close() throws Exception;

    /**
     * Does all the commands.
     * 
     * @param region the region
     * @throws Exception the exception
     */
    public abstract void doCommands(Region region) throws Exception;
}
