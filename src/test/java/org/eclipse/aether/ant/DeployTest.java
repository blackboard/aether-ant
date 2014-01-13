/*******************************************************************************
 * Copyright (c) 2010, 2014 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sonatype, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.aether.ant;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.util.Arrays;

/*
 * still missing:
 * - deploy snapshots/releases into correct repos
 */
public class DeployTest
    extends AntBuildsTest
{
    public void testDeployGlobalPom()
    {
        executeTarget( "testDeployGlobalPom" );
        long tstamp = System.currentTimeMillis();

        assertDebuglogContaining( "Uploading" );

        assertUpdatedFile( tstamp, distRepoDir, "test/dummy/0.1-SNAPSHOT/maven-metadata.xml" );
    }

    public void testDeployOverrideGlobalPom()
    {
        executeTarget( "testDeployOverrideGlobalPom" );
        long tstamp = System.currentTimeMillis();

        assertDebuglogContaining( "Uploading" );

        assertUpdatedFile( tstamp, distRepoDir, "test/other/0.1-SNAPSHOT/maven-metadata.xml" );
    }

    public void testDeployOverrideGlobalPomByRef()
    {
        long tstamp = System.currentTimeMillis();
        executeTarget( "testDeployOverrideGlobalPomByRef" );

        assertDebuglogContaining( "Uploading" );

        assertUpdatedFile( tstamp, distRepoDir, "test/dummy/0.1-SNAPSHOT/maven-metadata.xml" );
        assertUpdatedFile( tstamp, distRepoDir, "test/other/0.1-SNAPSHOT/maven-metadata.xml" );
    }

    public void testDeployAttachedArtifact()
    {
        executeTarget( "testDeployAttachedArtifact" );

        assertDebuglogContaining( "Uploading" );

        File dir = new File(distRepoDir, "test/dummy/0.1-SNAPSHOT/" );
        String[] files = dir.list();
        assertThat( "attached artifact not found: " + Arrays.toString( files ), files,
                    hasItemInArray( endsWith( "-ant.xml" ) ) );
    }

    private void assertUpdatedFile( long tstamp, File repoPath, String path )
    {
        File file = new File( repoPath, path );
        assertThat( "File does not exist in default repo: " + file.getAbsolutePath(), file.exists() );
        assertThat( "Files were not updated for 1s before/after timestamp",
                    file.lastModified(),
                    allOf( greaterThanOrEqualTo( ( ( tstamp - 500 ) / 1000 ) * 1000 ),
                           lessThanOrEqualTo( tstamp + 2000 ) ) );
    }
}
