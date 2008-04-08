begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|//Copyright 2003-2005 Arthur van Hoff, Rick Blair
end_comment

begin_comment
comment|//Licensed under Apache License version 2.0
end_comment

begin_comment
comment|//Original license LGPL
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|jmdns
package|;
end_package

begin_comment
comment|// REMIND: Listener should follow Java idiom for listener or have a different
end_comment

begin_comment
comment|//         name.
end_comment

begin_comment
comment|/**  * DNSListener.  * Listener for record updates.  *  * @author Werner Randelshofer, Rick Blair  * @version 1.0  May 22, 2004  Created.  */
end_comment

begin_interface
interface|interface
name|DNSListener
block|{
comment|/**      * Update a DNS record.      */
name|void
name|updateRecord
parameter_list|(
name|JmDNS
name|jmdns
parameter_list|,
name|long
name|now
parameter_list|,
name|DNSRecord
name|record
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

