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
package yadf.simulation.job.designation;

import yadf.simulation.IPlayer;
import yadf.simulation.IRegion;
import yadf.simulation.job.AbstractJob;
import yadf.simulation.job.BuildConstructionJob;
import yadf.simulation.map.BlockType;
import yadf.simulation.map.MapIndex;

/**
 * The Class ConstructionDesignation.
 */
public class ConstructionDesignation extends AbstractDesignation {

    /** The block type. */
    private final BlockType blockType;

    /**
     * Instantiates a new construction designation.
     * @param blockTypeTmp the block type
     * @param region the region that the designation is in
     * @param player the player that this designation belongs to
     */
    public ConstructionDesignation(final BlockType blockTypeTmp, final IRegion region, final IPlayer player) {
        super(region, player);
        blockType = blockTypeTmp;
    }

    @Override
    public String toString() {
        return blockType.name + " Construction Designation";
    }

    @Override
    protected AbstractJob createJob(final MapIndex mapIndex) {
        return new BuildConstructionJob(mapIndex, blockType, getRegion().getMap(), getPlayer());
    }

    @Override
    protected boolean valid(final MapIndex mapIndex) {
        return true;
    }
}
