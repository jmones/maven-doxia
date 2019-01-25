package org.apache.maven.doxia.module.markdown;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import com.vladsch.flexmark.ext.wikilink.internal.WikiLinkLinkResolver;
import com.vladsch.flexmark.html.IndependentLinkResolverFactory;
import com.vladsch.flexmark.html.LinkResolver;
import com.vladsch.flexmark.html.LinkResolverFactory;
import com.vladsch.flexmark.html.renderer.LinkResolverContext;
import com.vladsch.flexmark.html.renderer.LinkStatus;
import com.vladsch.flexmark.html.renderer.LinkType;
import com.vladsch.flexmark.html.renderer.ResolvedLink;
import com.vladsch.flexmark.util.ast.Node;

import java.util.HashSet;
import java.util.Set;

/**
 * The FlexmarkDoxiaLinkResolver rewrites .md and .markdown links to .html
 */
public class FlexmarkDoxiaLinkResolver implements LinkResolver
{
    final String[] inputFileExtensions;

    public FlexmarkDoxiaLinkResolver( LinkResolverContext context )
    {
        this.inputFileExtensions = new String[] {
                                        MarkdownParserModule.FILE_EXTENSION,
                                        MarkdownParserModule.ALTERNATE_FILE_EXTENSION
                                    };
    }

    @Override
    public ResolvedLink resolveLink( Node node, LinkResolverContext context, ResolvedLink link )
    {
        if ( link.getLinkType() == LinkType.LINK )
        {
            for ( String inputFileExtension : inputFileExtensions )
            {
                String url = link.getUrl();
                if ( !url.startsWith( "http://" ) && !url.startsWith( "https://" ) )
                {
                    if ( url.endsWith( "." + inputFileExtension ) )
                    {
                        url = url.substring( 0, url.length() - inputFileExtension.length() ) + "html";
                        return link.withStatus( LinkStatus.VALID ).withUrl( url );
                    }
                    else
                    {
                        if ( url.contains( "." + inputFileExtension + "#" ) )
                        {
                            url = url.replace( "." + inputFileExtension + "#", ".html#" );
                            return link.withStatus( LinkStatus.VALID ).withUrl( url );
                        }
                    }
                }
            }
        }

        return link;
    }

    /**
     * Factory that creates FlexmarkDoxiaLinkResolver objects.
     */
    public static class Factory extends IndependentLinkResolverFactory
    {
        @Override
        public Set<Class<? extends LinkResolverFactory>> getBeforeDependents()
        {
            Set<Class<? extends LinkResolverFactory>> set = new HashSet<Class<? extends LinkResolverFactory>>();
            set.add( WikiLinkLinkResolver.Factory.class );
            return set;
        }

        @Override
            public LinkResolver create( LinkResolverContext context )
        {
            return new FlexmarkDoxiaLinkResolver( context );
        }
    }
}
