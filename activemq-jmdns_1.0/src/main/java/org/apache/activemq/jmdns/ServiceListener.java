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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EventListener
import|;
end_import

begin_comment
comment|/**  * Listener for service updates.  *  * @version %I%, %G%  * @author	Arthur van Hoff, Werner Randelshofer  */
end_comment

begin_interface
specifier|public
interface|interface
name|ServiceListener
extends|extends
name|EventListener
block|{
comment|/**      * A service has been added.      *      * @param event The ServiceEvent providing the name and fully qualified type      *              of the service.      */
name|void
name|serviceAdded
parameter_list|(
name|ServiceEvent
name|event
parameter_list|)
function_decl|;
comment|/**      * A service has been removed.      *      * @param event The ServiceEvent providing the name and fully qualified type      *              of the service.      */
name|void
name|serviceRemoved
parameter_list|(
name|ServiceEvent
name|event
parameter_list|)
function_decl|;
comment|/**      * A service has been resolved. Its details are now available in the      * ServiceInfo record.      *      * @param event The ServiceEvent providing the name, the fully qualified      *              type of the service, and the service info record, or null if the service      *              could not be resolved.      */
name|void
name|serviceResolved
parameter_list|(
name|ServiceEvent
name|event
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

